from jmsserver.EpredCaller import Caller
from jmsserver.Form import Form
import jmsserver.EpredOption as EpOp
from zipfile import ZipFile
import flask, os, json

jms_server = flask.Flask(__name__)

jms_server.config['INPUT_DATA_DIR'] = os.path.join(
    jms_server.root_path, 'epred/input_data/')

jms_server.config['OUTPUT_FILE_DIR'] = os.path.join(
    jms_server.root_path, 'epred/output_data/')

jms_server.config['JAR_EXECUTABLE_DIR'] = os.path.join(
    jms_server.root_path, 'epred/epred_wrapper/xxx.jar')

jms_server.config['OUTPUT_REPORT_DIR'] = os.path.join(
    jms_server.config['OUTPUT_FILE_DIR'], 'Report/')

jms_server.config['REPORT_ZIP_DIR'] = os.path.join(
    jms_server.root_path, 'report/')

jms_server.config['REPORT_ZIP_NAME'] = 'report.zip'


@jms_server.route('/')
def index():
    return flask.render_template('index.html')


def _add_report_to_zip(report_zip, task_option):
    report_subdir = {EpOp.TASK_PREDICT: 'Pred',
                     EpOp.TASK_PRECISION_CHECK: 'Check',
                     EpOp.TASK_ANALYZE: 'Analysis'}[task_option]

    absolute_report_dir = os.path.join(
        jms_server.config['OUTPUT_REPORT_DIR'], report_subdir)
    report_names = os.listdir(absolute_report_dir)
    for report_name in report_names:
        report_path = os.path.join(absolute_report_dir, report_name)
        report_zip.write(report_path, os.path.basename(report_path))


def _pack_reports(report_zip, report_subdirs):
    for report_subdir in report_subdirs:
        _add_report_to_zip(report_zip, report_subdir)


def perform_task(option_set, year, report_zip):
    caller = Caller(jms_server.config['JAR_EXECUTABLE_DIR'],
                    jms_server.config['INPUT_DATA_DIR'],
                    jms_server.config['OUTPUT_FILE_DIR'])

    task_option = EpOp.get_task(option_set)
    task_name = {EpOp.TASK_PREDICT: 'predict', EpOp.TASK_ANALYZE: 'analyze',
                 EpOp.TASK_PRECISION_CHECK: 'check'}[task_option]

    task_result = {}
    success = False
    if caller.call(option_set, year) == 0:
        success = True
        _add_report_to_zip(report_zip, task_option)
    else:
        success = False

    task_result['success'] = success
    task_result['message'] = caller.get_log()
    return success, {task_name: task_result}


@jms_server.route('/upload_param', methods=['POST'])
def do_prediction():
    form = Form(flask.request.form)
    option_list = form.get_options()

    result_json = {}
    success = True

    with ZipFile(
            os.path.join(
                jms_server.config['REPORT_ZIP_DIR'],
                jms_server.config['REPORT_ZIP_NAME']), 'w') as report_zip:
        for option_set, year in option_list:
            # TODO read from database
            task_result, success = perform_task(option_set, year, report_zip)
            result_json.update(task_result)
            # TODO write to database

    return json.dumps(result_json), 200


@jms_server.route('/download_report')
def download_report():
    return flask.send_from_directory(
        jms_server.config['REPORT_ZIP_DIR'],
        jms_server.config['REPORT_ZIP_NAME'])
