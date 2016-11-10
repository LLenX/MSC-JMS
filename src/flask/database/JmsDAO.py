import mysql.connector
from JmsDataHelper import JmsDataHelper

"""
Jms Database Access Object
数据库读写主要对象。
"""


class JmsDAO:
    USER_NAME = 'test'
    PASSWORD = 'password'

    def __init__(self, db_name, username=USER_NAME, password=PASSWORD):
        JmsDAO.create_db_if_not_exists(db_name)
        self.db_conn = mysql.connector.connect(host="localhost", user=username, passwd=password,
                                               db=db_name,
                                               charset='utf8')

    def get_data_helper(self, option_set):
        """
        获取选项的数据管理器
        :param option_set: 选项
        :return:
        """
        return JmsDataHelper(option_set, self.db_conn)

    def import_all_input_files(self, folder_path):
        helper = JmsDataHelper(0, self.db_conn)
        helper.input_files = {
            '总用电量': '总用电量.xlsx',
            '分镇街用电量': '分镇街用电量.xlsx',
            '宏观数据': '宏观数据.xlsx',
        }
        helper.import_input_files(folder_path)

    def close(self):
        """
        关闭数据库连接
        :return:
        """
        self.db_conn.close()

    @staticmethod
    def create_db_if_not_exists(db_name):
        database = mysql.connector.connect(host="localhost", user=JmsDAO.USER_NAME, passwd=JmsDAO.PASSWORD)
        cursor = database.cursor()
        sql = 'CREATE DATABASE IF NOT EXISTS ' + db_name
        cursor.execute(sql)
        database.close()
