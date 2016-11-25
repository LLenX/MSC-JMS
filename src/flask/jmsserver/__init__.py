from jmsserver.EpredCaller import Caller
from jmsserver.Form import Form
import jmsserver.EpredOption as EpOp
from zipfile import ZipFile
import flask, os, json
from jmsserver.database.JmsDAO import JmsDAO
from jmsserver.database.UserDao import UserDao

jms_server = flask.Flask(__name__)

jms_server.config['USER_FOLDER'] = os.path.join(jms_server.root_path, 'user/')

jms_server.config['JAR_EXECUTABLE_DIR'] = os.path.join(
    jms_server.root_path, 'epred/epred_wrapper/xxx.jar')

jms_server.config['RFILE_DIR'] = os.path.join(
    jms_server.root_path, 'epred/input_data/Rfile/')

jms_server.config['MODEL_DIR'] = os.path.join(
    jms_server.root_path, 'epred/input_data/Model/')

jms_server.config['DATABASE_NAME'] = 'jms'


@jms_server.route('/')
def index():
    if not 'login' in flask.session:
        return flask.redirect(flask.url_for('login'))
    return flask.render_template('index.html')


def _ensure_directory(dir_path):
    if not os.path.exists(dir_path):
        os.mkdir(dir_path)

def _init_user_session(username):
    _ensure_directory(jms_server.config['USER_FOLDER'])

    user_home = os.path.join(jms_server.config['USER_FOLDER'], username)
    flask.session['HOME'] = user_home
    _ensure_directory(flask.session['HOME'])
    # where input file located
    flask.session['INPUT_FILES'] = os.path.join(user_home, 'input-files/')
    _ensure_directory(flask.session['INPUT_FILES'])

    # where output files would go
    flask.session['OUTPUT_FILES'] = os.path.join(user_home, 'output-files/')
    flask.session['OUTPUT_REPORT_DIR'] = os.path.join(
        flask.session['OUTPUT_FILES'], 'Report/')
    _ensure_directory(flask.session['OUTPUT_FILES'])

    # where the zip of reports would go
    flask.session['REPORT_ZIP_PATH'] = os.path.join(
        user_home, 'report/report.zip')
    _ensure_directory(os.path.dirname(flask.session['REPORT_ZIP_PATH']))


@jms_server.route('/login', methods=['GET', 'POST'])
def login():
    if flask.request.method == 'GET':
        if 'login' in flask.session:
            return flask.redirect(flask.url_for('index'))
        return flask.render_template('login.html')
    # flask.request.method == 'POST'

    # login validate username/password
    user_database = UserDao(jms_server.config['DATABASE_NAME'],
                            jms_server.config['DATABASE_USERNAME'],
                            jms_server.config['DATABASE_PASSWORD'])

    if user_database.get_user(
            flask.request.form['username'],
            flask.request.form['password']) is None:
        # handle login fail
        return '', 500

    flask.session['login'] = True
    flask.session['username'] = flask.request.form['username']
    _init_user_session(flask.session['username'])
    return flask.redirect(flask.url_for('index'))


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
        flask.session['OUTPUT_REPORT_DIR'], report_subdir)
    _add_folder_file_to_zip(report_zip, absolute_report_dir)


def _perform_task(option_set, year, report_zip):
    caller = Caller(
        jms_server.config['JAR_EXECUTABLE_DIR'],
        flask.session['INPUT_FILES'], flask.session['OUTPUT_FILES'],
        jms_server.config['MODEL_DIR'], jms_server.config['RFILE_DIR'])

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

    return {task_name: task_result}


@jms_server.route('/upload_param', methods=['POST'])
def do_prediction():
    result_json = {}

    with ZipFile(flask.session['REPORT_ZIP_PATH'], 'w') as report_zip:

        option_list = Form(flask.request.form).get_options()

        database_access_obj = JmsDAO(
            db_name=jms_server.config['DATABASE_NAME'],
            username=jms_server.config['DATABASE_USERNAME'],
            password=jms_server.config['DATABASE_PASSWORD'])

        for option_set, year in option_list:
            db_helper = database_access_obj.get_data_helper(option_set)
            db_helper.prepare_input_files(flask.session['INPUT_FILES'])

            task_result = _perform_task(option_set, year, report_zip)
            result_json.update(task_result)

            db_helper.collect_output_files(flask.session['OUTPUT_FILES'])

    return json.dumps(result_json), 200


@jms_server.route('/download_report')
def download_report():
    report_zip_path = flask.session['REPORT_ZIP_PATH']
    return flask.send_from_directory(
        os.path.dirname(report_zip_path), os.path.basename(report_zip_path))


# for session
jms_server.secret_key = 'temporary secret key'
