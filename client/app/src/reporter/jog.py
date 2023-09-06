import json

from websocket import create_connection
from ..user import User

import random


class Jog:

    def __init__(self, user: User):
        self.user = user
        self.ws = create_connection(f'ws://localhost:8080/event/jog?accessToken={self.user.token}')

    def publish(self):
        payload = {
            'step': 1,
            'distance': random.randint(0, 5),
        }

        message = {
            'message': {
                'value': {
                    'eventName': 'jog',
                    'payload': payload
                }
            }
        }

        print(self.ws.send(json.dumps(message)))

