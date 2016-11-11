# coding: utf-8

from .DbSheet import DbSheet
from jmsserver.EpredOption import *
import jmsserver.database.DbBin as DbBin
import jmsserver.database.CsvSheet as CsvSheet
import xlrd
import xlsxwriter
import os


class JmsDataHelper:
    def __init__(self, option_set, db_conn):
        """
        :param option_set: 选项，参见 EpredOption
        :param db_conn: 数据库连接
        """
        self.option_set = option_set
        self.input_files = JmsDataHelper.get_input_files(option_set)
        self.output_files = JmsDataHelper.get_output_files(option_set)
        self.db_bin = DbBin.DbBin(db_conn, 'raw_files')
        self.db_conn = db_conn

    def prepare_input_files(self, input_folder_path):
        """
        为EpredMain的运行准备输入文件
        :param input_folder_path: 输入文件文件夹，以 / 或 \ 结束
        :return:
        """
        self.export_files(input_folder_path, self.input_files)

    def collect_output_files(self, output_folder_path):
        """
        将EpredMain的输出结果保存到数据库
        :param output_folder_path: 输出文件夹路径，以 / 或 \ 结束
        :return:
        """
        self.import_files(output_folder_path, self.output_files)

    def import_input_files(self, folder_path):
        """
        导入输入文件。将选项所需的输入文件导入到数据库
        :param folder_path: 输入文件夹路径，以 / 或 \ 结束
        :return:
        """
        self.import_files(folder_path, self.input_files)

    def export_output_files(self, folder_path):
        """
        导出输出文件。将数据库保存的输出文件导出到文件系统。
        :param folder_path: 要导出的文件夹路径，以 / 或 \ 结束
        :return:
        """
        self.export_files(folder_path, self.output_files)

    def export_files(self, folder_path, files):
        for table_name in files:
            self.export_file(table_name, folder_path + files[table_name])

    def export_file(self, name, path):
        JmsDataHelper.ensure_folder_of_path(path)
        db_sheet = DbSheet(self.db_conn, name)
        if path.endswith('.csv'):
            writable_sheet = CsvSheet.CsvWritableSheet(path)
            db_sheet.transfer_db_to_excel(writable_sheet)
            writable_sheet.close()
        elif path.endswith('.xls') or path.endswith('.xlsx'):
            workbook = xlsxwriter.Workbook(path)
            writable_sheet = workbook.add_worksheet()
            db_sheet.transfer_db_to_excel(writable_sheet)
            workbook.close()
        else:
            f = open(path, 'wb')
            self.db_bin.transfer_db_to_bin_file(f, name)
            f.close()

    def import_files(self, folder_path, files):
        for table_name in files:
            self.import_file(table_name, folder_path + files[table_name])
        self.db_conn.commit()

    def import_file(self, name, path):
        if path.endswith('.csv') or path.endswith('.xls') or path.endswith('.xlsx'):
            if path.endswith('.csv'):
                readable_sheet = CsvSheet.CsvReadableSheet(path)
            else:
                readable_sheet = xlrd.open_workbook(path, encoding_override="utf-8").sheets()[0]
            db_sheet = DbSheet(self.db_conn, name)
            db_sheet.drop_table_if_exists()
            db_sheet.transfer_excel_to_db(readable_sheet=readable_sheet, column_names=None, commit=False)
        else:
            f = open(path, 'rb')
            self.db_bin.delete_bin_file_in_db_if_exists(name)
            self.db_bin.transfer_bin_file_to_db(f, name)
            f.close()

    @staticmethod
    def ensure_folder_of_path(path):
        i = path.rfind('\\')
        if i < 0:
            i = path.rfind('/')
        if i < 0:
            return
        directory = path[:i + 1]
        if not os.path.exists(directory):
            os.makedirs(directory)

    """
    输入文件表，描述各个选项需要的输入文件。
    每个项目为 "文件名" : "文件相对路径"
    结果文件中的xlxs、csv会以数据库表的形式保存
    其他格式的文件则以二进制形式保存
    """
    i_table = {
        AREA_ALL: {
            '总用电量': '总用电量.xlsx',
        },
        AREA_TOWN: {
            '分镇街用电量': '分镇街用电量.xlsx',
        },
        AREA_VICE_MODEL: {
            '宏观数据': '宏观数据.xlsx',
        }
    }

    o_table = {

    }

    @staticmethod
    def get_input_files(option_set):
        # 反正输入文件只有这三个，全部都给他好了....
        return {
            '总用电量': '总用电量.xlsx',
            '分镇街用电量': '分镇街用电量.xlsx',
            '宏观数据': '宏观数据.xlsx',
        }

    @staticmethod
    def get_output_files(option_set):
        return {}  # JmsDataHelper.o_table[option_set]
