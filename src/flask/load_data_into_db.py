#! /usr/bin/python3
from jmsserver.database.JmsDAO import JmsDAO
import sys
import json
import os
from getpass import getpass

if __name__ == '__main__':
    if len(sys.argv) != 2:
        print("usage: load_data_into_db <config_file>", file=sys.stderr)
        exit(1)

    if not os.path.exists(sys.argv[1]):
        print("config file: %s not found" % sys.argv[1], file=sys.stderr)
        exit(1)

    # may go wrong here, but i'm too lazy, so be careful yourself dude
    config_dict = json.load(open(sys.argv[1]))

    try:
        username = config_dict['database_username']
    except KeyError:
        print("no username of database")
        sys.exit(1)

    try:
        filepath = config_dict['data_path']
    except KeyError:
        print("no path of the data")
        sys.exit(1)

    try:
        db_name = config_dict['database_name']
    except KeyError:
        print("no database name")
        sys.exit(1)

    try:
        password = getpass('password for db:')
        jms_data_obj = JmsDAO(
            db_name=db_name, username=username, password=password)
        jms_data_obj.import_all_input_files(filepath)
    except Exception:
        print('something goes wrong...')
