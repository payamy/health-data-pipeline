import requests


class User:

    def __init__(self,
                 username,
                 password,
                 name,
                 eye_color,
                 blood_type):
        self.username = username
        self.__password = password
        self.name = name
        self.__eye_color = int(eye_color)
        self.__blood_type = int(blood_type)
        self.token = ''

        self.payload = {
            'username': self.username,
            'password': self.__password,
            'name': self.name,
            'eyeColor': self.__eye_color,
            'bloodType': self.__blood_type,
        }
        self.__create_user_if_not_exists()

    def __create_user_if_not_exists(self):
        obj = requests.post(url='http://localhost:8080/api/auth/signup/', json=self.payload)
        if obj.status_code == 200:
            self.token = self.__get_user_token()
            return
        else:
            try:
                self.token = self.__get_user_token()
                return
            except Exception as e:
                print(f'Invalid Payload: {e}')

    def __get_user_token(self):
        obj = requests.post(
            url='http://localhost:8080/api/auth/login/',
            json={
                'username': self.username,
                'password': self.__password,
            }
        )
        if obj.status_code == 200:
            return obj.json()['token']
        raise Exception('User Credentials Are Incorrect')
