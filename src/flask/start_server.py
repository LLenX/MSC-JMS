#! /usr/bin/python3

from jmsserver import jms_server
import os
import sys
import json
from getpass import getpass
# from getopt import gnu_getopt

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

def get_config_from_file(config_file_name='serverconf.json'):
    config_dict = {}
    try:
        config_file = open(config_file_name)
        config_dict = json.load(config_file)
    except:
        print('Warning: something goes wrong with configuration file...',
              file=sys.stderr)
    finally:
        return config_dict


if __name__ == '__main__':
    config_dict = get_config_from_file()
    start_r_server()

    port = config_dict.get('port') or 12345
    host = config_dict.get('host') or '127.0.0.1'
    jms_server.config['DATABASE_USERNAME'] = config_dict.get(
        'database_username') or 'root'

    jms_server.config['DATABASE_PASSWORD'] = getpass('password for db: ')

    jms_server.run(debug=True, port=port, host=host)
