from flask import Flask, render_template

jms_server = Flask(__name__)


@jms_server.route('/')
def index():
    return render_template('index.html')


@jms_server.route('/upload_param', methods=['GET', 'POST'])
def do_prediction():
    return 'predict ok'


@jms_server.route('/upload_param', methods=['GET', 'POST'])
def upload_file():
    return 'upload ok'


jms_server.run(debug=True)
