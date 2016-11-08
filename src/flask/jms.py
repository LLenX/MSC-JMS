from flask import Flask, render_template, redirect, url_for
import os.path

jms_server = Flask(__name__)


@jms_server.route('/')
def index():
    return render_template('index.html')


@jms_server.route('/upload_param', methods=['GET', 'POST'])
def do_prediction():
    return 'hahaha'


@jms_server.route('/upload_param', methods=['GET', 'POST'])
def upload_file():
    return 'hahaha'


# temporary function
@jms_server.route('/<path:filename>')
def get_file(filename):
    ext_tup = os.path.splitext(filename)
    print(ext_tup)
    if ext_tup[1] != '.css' and ext_tup[1] != '.js':
        return redirect(url_for('do_prediction'))

    return open(
        os.path.join(jms_server.root_path, 'templates/', filename)).read()


jms_server.run(debug=True)
