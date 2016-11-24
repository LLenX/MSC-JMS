PERMISSION_USER = 1
PERMISSION_ADMIN = 2

UID_NONE = -1


class User:
    def __init__(self, name, uid=UID_NONE, permission=PERMISSION_USER):
        self.uid = uid
        self.name = name
        self.permission = permission

    def __str__(self):
        return "User{name='%s', permission=%s, uid=%d}" % (self.name, User.permission_to_str(self.permission), self.uid)

    @staticmethod
    def permission_to_str(permission):
        return {
            PERMISSION_ADMIN: 'PERMISSION_ADMIN',
            PERMISSION_USER: 'PERMISSION_USER'
        }[permission]
