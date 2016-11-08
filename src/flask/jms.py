from EpredCaller import Caller
import flask, os, os.path
import EpredOption as EpOp

jms_server = flask.Flask(__name__)

jms_server.config['INPUT_DATA_DIR'] = os.path.join(jms_server.root_path,
                                                   'input_data/')
jms_server.config['OUTPUT_DATA_DIR'] = os.path.join(jms_server.root_path,
                                                    'output_data/')
jms_server.config['JAR_EXECUTABLE_DIR'] = os.path.join(jms_server.root_path,
                                                       'epred_wrapper/xxx.jar')


@jms_server.route('/')
def index():
    return flask.render_template('index.html')


def _get_option_from_form_value(area_str, duration_str, time_str):
    form_option_map = {
        '请选择': 0,
        '全年': 0,
        '全社会用电量': EpOp.AREA_ALL,
        '分镇街用电量': EpOp.AREA_TOWN,
        '副模型': EpOp.AREA_VICE_MODEL,
        '年度': EpOp.DURATION_ALL_YEAR,
        '半年度': EpOp.DURATION_SEMI_YEAR,
        '季度': EpOp.DURATION_SEASON,
        '上半年': EpOp.TIME_FIRST_HALF,
        '下半年': EpOp.TIME_SECOND_HALF,
        '第一季度': EpOp.TIME_FIRST_SEASON,
        '第二季度': EpOp.TIME_SECOND_SEASON,
        '第三季度': EpOp.TIME_THIRD_SEASON,
        '第四季度': EpOp.TIME_FOURTH_SEASON
    }
    area = form_option_map[area_str]
    duration = form_option_map[duration_str]
    time = form_option_map[time_str]

    return area | duration | time


@jms_server.route('/upload_param', methods=['GET', 'POST'])
def do_prediction():
    caller = Caller(jms_server.config['JAR_EXECUTABLE_DIR'],
                    jms_server.config['INPUT_DATA_DIR'],
                    jms_server.config['OUTPUT_DATA_DIR'])
    if flask.request.form.get('00') == 'true':  # 00 predict
        options = _get_option_from_form_value(flask.request.form['01'],
                                              flask.request.form['03'],
                                              flask.request.form['04'])
        year = flask.request.form.get('02')
        if year != '请选择':
            caller.predict(year, options)

    if flask.request.form.get('10') == 'true':
        options = _get_option_from_form_value(flask.request.form['11'],
                                              flask.request.form['13'],
                                              flask.request.form['14'])
        year = flask.request.form.get('12')
        if year != '请选择':
            caller.check_precision(year, options)

    if flask.request.form.get('20') == 'true':
        caller.associativity_analysis()

    return 'ok'


def start_r_server():
    os.system('killall Rserve')
    os.system('Rscript -e " library(Rserve);Rserve()" &> /dev/null')


if __name__ == '__main__':
    # start_r_server()
    jms_server.run(debug=True)
