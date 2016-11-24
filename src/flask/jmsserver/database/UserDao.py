import mysql.connector
import jmsserver.database.DatabaseHelper
from .User import User


class UserDao:
    def __init__(self, db_name, username, password):
        """
        :param db_name: 数据库名称
        :param username: 数据库用户名
        :param password: 数据库密码
        """
        DatabaseHelper.create_db_if_not_exists(db_name, username, password)
        self.table_name = 'user'
        self.db_conn = mysql.connector.connect(host="localhost", user=username, passwd=password,
                                               db=db_name,
                                               charset='utf8')
        cursor = self.db_conn.cursor()
        cursor.execute('CREATE TABLE IF NOT EXISTS ' + self.table_name + """ (
                uid INT NOT NULL AUTO_INCREMENT,
                username TEXT NOT NULL,
                password VARCHAR(32),
                permission INT NOT NULL,
                PRIMARY KEY (uid)
        ) """)

    def add_user(self, user, password):
        """
        新增用户
        :param user: User实例
        :param password: 密码。长度<=32的字符串。
        :return: 是否新增成功。用户名已存在时失败。
        """
        if self.has_user(user.name):
            return False
        cursor = self.db_conn.cursor()
        cursor.execute("INSERT INTO " + self.table_name + " (username, password, permission) VALUES ('%s', '%s', %d)" %
                       (user.name, password, user.permission))
        self.db_conn.commit()
        return True

    def delete_user(self, username):
        """
        删除用户
        :param username: 要删除的用户的用户名，
        :return: 是否删除成功。用户名不存在时失败。
        """
        if not self.has_user(username):
            return False
        cursor = self.db_conn.cursor()
        cursor.execute("DELETE FROM " + self.table_name + " WHERE username = '%s'" % username)
        self.db_conn.commit()
        return True

    def update_user(self, user, password=None):
        """
        更新用户信息
        :param user: 要更新的User实例。将以用户名为依据更新信息。
        :param password: 新密码
        :return: 是否更新成功
        """
        if not self.has_user(user.name):
            return False
        cursor = self.db_conn.cursor()
        if password is None:
            cursor.execute("UPDATE " + self.table_name + " SET permission = %d WHERE username = '%s'" % (
                user.permission, user.name))
        else:
            cursor.execute(
                "UPDATE " + self.table_name + " SET password = '%s', permission = %d WHERE username = '%s'" % (
                    password, user.permission, user.name))
        self.db_conn.commit()
        return True

    def get_user(self, username, password):
        """
        根据用户名和密码获取用户
        :param username: 用户名
        :param password: 密码
        :return: User实例
        """
        cursor = self.db_conn.cursor()
        cursor.execute(
            "SELECT username, uid, permission FROM " + self.table_name + " WHERE username = '%s' AND password = '%s'" %
            (username, password))
        result = cursor.fetchone()
        if result is None:
            return None
        return User(result[0], result[1], result[2])

    def has_user(self, username):
        """
        判断用户名是否存在
        :param username: 用户名
        :return: 存在返回True
        """
        cursor = self.db_conn.cursor()
        cursor.execute(
            "SELECT * FROM " + self.table_name + " WHERE username = '%s'" % username)
        return cursor.fetchone() is not None

    def close(self):
        self.db_conn.close()
