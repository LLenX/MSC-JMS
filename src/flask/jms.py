from EpredCaller import Caller
from Form import Form
from zipfile import ZipFile
import flask, os, os.path, sys

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


@jms_server.route('/')
def index():
    return flask.render_template('index.html')


@jms_server.route('/upload_param', methods=['GET', 'POST'])
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

    # with ZipFile(os.path.join(jms_server.config), 'w') as report_zip:
    #     for subdir in report_subdirs:
    #         report_dir = os.path.join(
    #                 jms_server.config['OUTPUT_REPORT_DIR'], subdir)


    return 'ok'


def start_r_server():
    os.system('killall Rserve &> /dev/null')
    if os.system('Rscript -e "library(Rserve); Rserve()"') != 0:
        print('Fatal: Rserve didn\'t start correctly', file=sys.stderr,
              flush=True)
        sys.exit(1)
    else:
        print('Rserver start')
    sys.stdout.flush()
    sys.stderr.flush()


if __name__ == '__main__':
    start_r_server()
    jms_server.run(debug=True)
