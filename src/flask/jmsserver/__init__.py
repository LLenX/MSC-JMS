from jmsserver.EpredCaller import Caller
from jmsserver.Form import Form
import jmsserver.EpredOption as EpOp
from zipfile import ZipFile
import flask, os, json
from jmsserver.database.JmsDAO import JmsDAO

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

jms_server.config['DATABASE_NAME'] = 'jms'


@jms_server.route('/')
def index():
    return flask.render_template('index.html')


def _add_folder_file_to_zip(file_zip, file_folder_path):
    file_names = os.listdir(file_folder_path)
    for file_name in file_names:
        file_path = os.path.join(file_folder_path, file_name)
        file_zip.write(file_path, os.path.basename(file_path))


def _add_report_to_zip(report_zip, task_option):
    report_subdir = {EpOp.TASK_PREDICT: 'Pred',
                     EpOp.TASK_PRECISION_CHECK: 'Check',
                     EpOp.TASK_ANALYZE: 'Analysis'}[task_option]

    absolute_report_dir = os.path.join(
        jms_server.config['OUTPUT_REPORT_DIR'], report_subdir)
    _add_folder_file_to_zip(report_zip, absolute_report_dir)


def _ensure_directories():
    if not os.path.exists(jms_server.config['OUTPUT_FILE_DIR']):
        os.makedirs(jms_server.config['OUTPUT_FILE_DIR'])

    if not os.path.exists(jms_server.config['REPORT_ZIP_DIR']):
        os.makedirs(jms_server.config['REPORT_ZIP_DIR'])


def _perform_task(option_set, year, report_zip):
    caller = Caller(
        jms_server.config['JAR_EXECUTABLE_DIR'],
        jms_server.config['INPUT_DATA_DIR'],
        jms_server.config['OUTPUT_FILE_DIR'])

    task_option = EpOp.get_task(option_set)
    task_name = {
        EpOp.TASK_PREDICT: 'predict', EpOp.TASK_ANALYZE: 'analyze',
        EpOp.TASK_PRECISION_CHECK: 'check'}[task_option]

    task_result = {}

    success = caller.call(option_set, year) == 0
    if success:
        _add_report_to_zip(report_zip, task_option)

    task_result['success'] = success
    task_result['message'] = caller.get_log()

    return {task_name: task_result}, success


@jms_server.route('/upload_param', methods=['POST'])
def do_prediction():
    _ensure_directories()

    result_json = {}

    with ZipFile(
            os.path.join(
                jms_server.config['REPORT_ZIP_DIR'],
                jms_server.config['REPORT_ZIP_NAME']), 'w') as report_zip:

        option_list = Form(flask.request.form).get_options()

        database_access_obj = JmsDAO(
            db_name=jms_server.config['DATABASE_NAME'],
            username=jms_server.config['DATABASE_USERNAME'],
            password=jms_server.config['DATABASE_PASSWORD'])

        for option_set, year in option_list:
            db_helper = database_access_obj.get_data_helper(option_set)
            db_helper.prepare_input_files(jms_server.config['INPUT_DATA_DIR'])

            task_result, success = _perform_task(option_set, year, report_zip)
            result_json.update(task_result)

            db_helper.collect_output_files(jms_server.config['OUTPUT_FILE_DIR'])

    return json.dumps(result_json), 200


@jms_server.route('/download_report')
def download_report():
    return flask.send_from_directory(
        jms_server.config['REPORT_ZIP_DIR'],
        jms_server.config['REPORT_ZIP_NAME'])
