import json

from ..user import User
from websocket import create_connection

import random


class Heartbeat:

    def __init__(self, user: User):
        self.user = user
        self.token = self.user.token

        self.__oxygen_values = [94, 95, 96, 97, 98]
        self.__oxygen_distribution = (1, 20, 50, 100, 50)

        self.__temperature_values = [37, 38, 39, 40]
        self.__temperature_distribution = (200, 20, 5, 1)

        self.ws = create_connection(f'ws://localhost:8080/event/heartbeat?accessToken={self.user.token}')

    def publish(self):

        oxygen_value = random.choices(
            self.__oxygen_values,
            weights=self.__oxygen_distribution,
            k=1
        )[0]

        temperature_value = random.choices(
            self.__temperature_values,
            weights=self.__temperature_distribution,
            k=1
        )[0]

        payload = {
            'heartbeat': random.randint(70, 90),
            'oxygen': oxygen_value,
            'temperature': temperature_value,
        }

        message = {
            'message': {
                'value': {
                    'eventName': 'heartbeat',
                    'payload': payload
                }
            }
        }

        print(self.ws.send(json.dumps(message)))
