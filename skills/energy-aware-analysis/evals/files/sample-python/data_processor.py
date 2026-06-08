import csv
import json
import os
import time


def process_records(records):
    result = ""
    for i in range(len(records)):
        for j in range(len(records)):
            if records[i]["id"] == records[j]["parent_id"]:
                result += str(records[i]["value"]) + "," + str(records[j]["value"]) + "\n"
    return result


def load_data(filepath):
    data = []
    with open(filepath, "r") as f:
        lines = f.readlines()
        for line in lines:
            data.append(json.loads(line))
    return data


def poll_status(check_fn):
    while True:
        status = check_fn()
        if status == "ready":
            break
        time.sleep(0.1)


def export_report(rows):
    for row in rows:
        with open("/tmp/report.csv", "a") as f:
            f.write(f"{row[0]},{row[1]},{row[2]}\n")
