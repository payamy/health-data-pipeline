import asyncio
import argparse
import random

import datetime

from src.user import User
from src.reporter import *


async def send_heartbeat(heartbeat: Heartbeat):
    while True:
        heartbeat.publish()
        print(f'HEARTBEAT: Time {datetime.datetime.now()}')
        await asyncio.sleep(5)


async def send_jog(jog: Jog):
    while True:
        wait_seconds = random.uniform(0.001, 2)
        jog.publish()
        print(f'JOGGING (RUNNING): Time {datetime.datetime.now()}')
        await asyncio.sleep(wait_seconds)


async def main(username: str,
               password: str,
               name: str,
               eye_color: int,
               blood_type: int):

    user = User(
        username=username,
        password=password,
        name=name,
        eye_color=eye_color,
        blood_type=blood_type,
    )

    heartbeat = Heartbeat(user=user)
    jog = Jog(user=user)

    task_heartbeat = loop.create_task(send_heartbeat(heartbeat))
    task_jog = loop.create_task(send_jog(jog))
    await asyncio.wait([task_heartbeat, task_jog])


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-u', '--username', type=str, required=True)
    parser.add_argument('-p', '--password', type=str, required=True)
    parser.add_argument('-n', '--name', type=str, required=True)
    parser.add_argument('--eye_color', type=int, required=True)
    parser.add_argument('--blood_type', type=int, required=True)
    args = parser.parse_args()

    loop = asyncio.get_event_loop()
    loop.run_until_complete(main(**args.__dict__))
    loop.close()
