# coding: utf-8
import mysql.connector
import DatabaseHelper

"""
一个可以把二进制文件和数据库互相读写的类
"""


class DbBin:
    def __init__(self, db_conn, table_name):
        """
        :param db_conn: MySQL数据库连接
        :param table_name: 要读写的数据库表名
        """
        self.db_conn = db_conn
        self.table_name = table_name
        self.create_table_if_not_exists()

    def transfer_bin_file_to_db(self, bin_file, file_name, commit=True):
        """
        把二进制文件保存到数据库
        :param bin_file: 可读的二进制文件
        :param file_name: 文件名
        :param commit: 是否提交数据库写入
        :return:
        """
        b = bin_file.read()
        sql = "INSERT INTO " + self.table_name + " (file_name, bin_data) VALUES(%s, %s)"
        self.db_conn.cursor().execute(sql, (file_name, b))
        if commit:
            self.db_conn.commit()

    def transfer_db_to_bin_file(self, bin_file, file_name):
        """
        将数据库中的二进制数据写入到文件中
        :param bin_file: 可写文件
        :param file_name: 文件名
        :return:
        """
        sql = 'SELECT bin_data FROM ' + self.table_name + ' WHERE file_name = \'' + file_name + '\' limit 1'
        cursor = self.db_conn.cursor()
        cursor.execute(sql)
        data = cursor.fetchone()[0]
        bin_file.write(data)

    def delete_bin_file_in_db_if_exists(self, file_name):
        """
        将数据库中的二进制文件删除
        :param file_name: 文件名
        :return:
        """
        sql = 'DELETE FROM ' + self.table_name + " WHERE file_name = '%s'" % file_name
        self.db_conn.cursor().execute(sql)

    def drop_table_if_exists(self):
        """
        如果表存在则删除表
        :return:
        """
        DatabaseHelper.drop_table_if_exists(self.db_conn, self.table_name)

    def create_table_if_not_exists(self):
        """
        如果表不存在则创建表
        :return:
        """
        sql = 'CREATE TABLE IF NOT EXISTS ' + self.table_name + ' (file_name TEXT NOT NULL, bin_data MEDIUMBLOB)'
        self.db_conn.cursor().execute(sql)
