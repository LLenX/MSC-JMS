# coding: utf-8

"""
一个可以把excel表和数据库表互相读写的类
"""


class DbSheet:
    def __init__(self, db_conn, table_name):
        """
        :param db_conn: MySQL数据库连接
        :param table_name: 要读写的数据库表名
        """
        self.db_conn = db_conn
        self.table_name = table_name

    def drop_table_if_exists(self):
        """
        如果表存在则删除表
        :return:
        """
        cursor = self.db_conn.cursor()
        cursor.execute('DROP TABLE IF EXISTS ' + self.table_name)

    def create_table_if_not_exists(self, column_names):
        """
        如果表不存在则创建表
        :return:
        """
        cursor = self.db_conn.cursor()
        sql = 'CREATE TABLE IF NOT EXISTS ' + self.table_name + " (" + ' TEXT, '.join(column_names) + ' TEXT )'
        cursor.execute(sql)

    def transfer_excel_to_db(self, readable_sheet, column_names=None, commit=True):
        """
        把excel表写入到数据库（附加到数据库表末尾）
        :param readable_sheet: excel表, xlrd的worksheet
        :param column_names: 列名称，如果不指定，则把Excel表的第一行作为表头，从第二行开始作为数据
        :param commit: 是否提交数据库写入,为False则需要手动提交
        :return:
        """
        data_row_start = 0
        if column_names is None:
            column_names = self.get_column_name_from_sheet(readable_sheet)
            data_row_start = 1
        self.create_table_if_not_exists(column_names)
        cursor = self.db_conn.cursor()
        for i in range(data_row_start, readable_sheet.nrows):
            sql = "INSERT INTO " + self.table_name + "(" + ','.join(column_names) + ") VALUES ("
            sql += self.pack_cell(readable_sheet.cell(i, 0))
            for j in range(1, readable_sheet.ncols):
                sql += ',' + self.pack_cell(readable_sheet.cell(i, j))
            sql += ')'
            cursor.execute(sql)
        if commit:
            self.db_conn.commit()

    @staticmethod
    def get_column_name_from_sheet(readable_sheet):
        column_names = []
        for i in range(readable_sheet.ncols):
            column_names.append(DbSheet.cell_to_str(readable_sheet.cell(0, i)))
        return column_names

    @staticmethod
    def pack_cell(cell):
        return "'" + DbSheet.cell_to_str(cell) + "'"

    @staticmethod
    def cell_to_str(cell):
        if cell.ctype == 1:  # 1为Text，由于暂时找不到xlrd中哪个常量代表这个类型。暂时用裸数字
            return str(cell.value.encode('utf-8'))
        else:
            return str(cell.value)

    def transfer_db_to_excel(self, writable_sheet, column_names=None):
        """
        把数据库表的数据写入到excel表中（覆盖excel表原数据）
        :param writable_sheet: xlsxwriter的worksheet
        :param column_names: 列名称，将会写入到excel表的第一行，如果不指定，则把数据库表的列名称写入
        :return:
        """
        if column_names is None:
            column_names = self.get_column_name_from_db()
        cursor = self.db_conn.cursor()
        sql = 'SELECT * FROM ' + self.table_name
        cursor.execute(sql)
        results = cursor.fetchall()
        col_count = 0
        for column_name in column_names:
            writable_sheet.write(0, col_count, column_name[0])
            col_count += 1
        row_count = 1
        for row in results:
            col_count = 0
            for cell in row:
                writable_sheet.write(row_count, col_count, cell)
                col_count += 1
            row_count += 1

    def get_column_name_from_db(self):
        sql = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.Columns WHERE TABLE_NAME = '%s'" % self.table_name
        cursor = self.db_conn.cursor()
        cursor.execute(sql)
        return cursor.fetchall()
