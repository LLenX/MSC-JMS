#! /usr/bin/python3
from jmsserver.database.JmsDAO import JmsDAO

if __name__ == '__main__':
    jms_data_obj = JmsDAO(db_name='jms', username='root', password='xx')
    jms_data_obj.import_all_input_files('jmsserver/epred/input_data/')