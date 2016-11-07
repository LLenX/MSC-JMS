# coding: utf-8

import MySQLdb
import xlsxwriter
import xlrd
from DbSheet import DbSheet

USER_NAME = 'test'
PASSWORD = 'password'
DB_NAME = 'excel'


def create_db_if_not_exists(db_name):
    database = MySQLdb.connect(host="localhost", user=USER_NAME, passwd=PASSWORD)
    cursor = database.cursor()
    sql = 'CREATE DATABASE IF NOT EXISTS ' + db_name
    cursor.execute(sql)
    database.close()


create_db_if_not_exists(DB_NAME)

db = MySQLdb.connect(host="localhost", user=USER_NAME, passwd=PASSWORD, db=DB_NAME, charset='utf8')

readable_sheet = xlrd.open_workbook(u'D:\\宏观数据.xlsx', encoding_override="utf-8").sheets()[0]

workbook = xlsxwriter.Workbook(r'D:\copy.xlsx')
writable_sheet = workbook.add_worksheet()

db_sheet = DbSheet(db, 'test')
db_sheet.drop_table_if_exists()
db_sheet.transfer_excel_to_db(readable_sheet)
db_sheet.transfer_db_to_excel(writable_sheet)

workbook.close()
db.close()
