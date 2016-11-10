from jmsserver.EpredCaller import Caller
from jmsserver.Form import Form
from zipfile import ZipFile
import flask, os, os.path

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


def _pack_reports(report_zip, report_subdirs):
    for report_subdir in report_subdirs:
        absolute_report_path = os.path.join(
            jms_server.config['OUTPUT_REPORT_DIR'], report_subdir)
        report_names = os.listdir(absolute_report_path)
        if report_names:
            # only one report per directory
            absolute_report_path = os.path.join(
                absolute_report_path, report_names[0])
            report_zip.write(
                absolute_report_path, os.path.basename(absolute_report_path))


@jms_server.route('/upload_param', methods=['POST'])
def do_prediction():
    caller = Caller(jms_server.config['JAR_EXECUTABLE_DIR'],
                    jms_server.config['INPUT_DATA_DIR'],
                    jms_server.config['OUTPUT_FILE_DIR'])

    form = Form(flask.request.form)

    report_subdirs = []

    if form.can_predict():
        year, options = form.predict_option()
        if caller.predict(year, options) == 0:
            report_subdirs.append('Pred')

    if form.can_check():
        year, options = form.check_option()
        if caller.check_precision(year, options) == 0:
            report_subdirs.append('Check')

    if form.can_analyze():
        if caller.associativity_analysis() == 0:
            report_subdirs.append('Analysis')

    return '', 204


@jms_server.route('/download_report')
def download_report():
    report_subdirs = ['Pred', 'Check', 'Analysis']
    if not os.path.exists(jms_server.config['REPORT_ZIP_DIR']):
        os.makedirs(jms_server.config['REPORT_ZIP_DIR'])
    with ZipFile(
            os.path.join(
                jms_server.config['REPORT_ZIP_DIR'],
                jms_server.config['REPORT_ZIP_NAME']), 'w') as report_zip:
        _pack_reports(report_zip, report_subdirs)

    return flask.send_from_directory(
        jms_server.config['REPORT_ZIP_DIR'],
        jms_server.config['REPORT_ZIP_NAME'])
