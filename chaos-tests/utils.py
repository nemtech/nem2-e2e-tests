#!/bin/python

import json
import yaml
import os
import sys
import logging
import fire
from pyjavaproperties import Properties
from symbol_data import SymbolDataMongo as sdm


def assert_total_transactions(before: int, injected: int, after: int):
    try:
        assert (before + injected) == after
        return 0
    except AssertionError:
        return 1


def injected_transactions(transaction_rate: int,
                          start_epoch: int,
                          end_epoch: int,
                          time_offset: int=0) -> int:
    duration = end_epoch - start_epoch - time_offset
    total_transactions = duration * transaction_rate
    return  total_transactions


def update_properties_file(properties_file, new_properties):
    p = Properties()
    logging.debug('Opening file %s', properties_file)
    p.load(open(properties_file))
    for name, value in new_properties:
        logging.info('Setting new value of %s to %s', name, value)
        p[name] = value
    logging.debug('Saving file %s with new values', properties_file)
    p.store(open(properties_file, 'w'))


def avoid_banning(symbol_server_dir):
    nodes_props_files = find_all_occurances_of_file(
        'config-node.properties.mt', ['..', symbol_server_dir])
    for node_props_file in nodes_props_files:
        update_properties_file(
            node_props_file, 
            [('trustedHosts', ''),
             ('localNetworks', '')
             ])


def get_docker_container_names(compose_file):
    with open(compose_file) as file:
        # The FullLoader parameter handles the conversion from YAML
        # scalar values to Python the dictionary format
        compose_yaml = yaml.load(file, Loader=yaml.FullLoader)
    services = compose_yaml['services']
    docker_containers = [v['command'].split(' ')[-1] for v in services.values()]
    # print(docker_containers)
    return docker_containers


def parse_pumba_logs(filename: str):
    with open(filename, 'r') as file:
        lines = file.readlines()
    killed_containers = []
    for line in lines:
        line_json = json.loads(line)
        if 'killing container' in line_json['msg']:
            container_name = line_json['name'].lstrip('/')
            killed_containers.append(container_name)
    return killed_containers


def get_relative_file_path(file_name, target_dir=None):
    if target_dir is not None:
        logging.debug('Looking for file %s under %s directory', file_name, target_dir)
        relative_file_path = find_all_occurances_of_file(file_name, ['..', target_dir])
    else:
        logging.debug('Looking for file %s', file_name)
        relative_file_path = [os.path.join(dirpath, filename)
                              for dirpath, _, filenames in os.walk('..')
                              for filename in filenames if filename.endswith(file_name)
                              ]
    if len(relative_file_path) > 1:
        logging.warning("More than one file with the same name found in sub-directories %s. Returning the first one.", relative_file_path)
        return relative_file_path[0]
    elif len(relative_file_path) == 0:
        logging.error("File '%s' not found!", file_name)
    else:
        return relative_file_path[0]


def find_all_occurances_of_file(file_name, target_dir):
    logging.debug('Looking for file %s under %s directory', file_name, '/'.join(target_dir))
    file_paths = [os.path.join(dirpath, filename)
                  for dirpath, _, filenames in os.walk(os.path.join(*target_dir))
                  for filename in filenames if filename.endswith(file_name)
                  ]
    return file_paths


def get_first_user_private_key():
    addresses_file = get_relative_file_path('addresses.yaml')
    logging.info('Path of addresses.yml is: {}'.format(addresses_file))
    with open(addresses_file) as file:
        # The FullLoader parameter handles the conversion from YAML
        # scalar values to Python the dictionary format
        addresses_yaml = yaml.load(file, Loader=yaml.FullLoader)
    # print(addresses_yaml['nemesis_addresses'][0]['private'])
    first_address_private_key = addresses_yaml['nemesis_addresses'][0]['private']
    return first_address_private_key


if __name__ == "__main__":
    fire.Fire()
