#!/bin/python

from pymongo import MongoClient
import logging
import sys
import fire


class SymbolDataMongo:

    def __init__(self, mongo_host='localhost', mongo_port=27017):
        self.db_host = mongo_host
        self.db_port = mongo_port
        self.db_client = MongoClient(self.db_host, self.db_port)
        self.symbol_db = self.db_client.catapult


    def print_database_names(self):
        print(self.db_client.list_database_names())


    def print_symbol_collections(self):
        print(self.symbol_db.list_collection_names())


    def count_transactions(self):
        trans_count = self.symbol_db.transactions.count_documents({})
        logging.info("Count of transactions: %s", trans_count)
        return trans_count


if __name__ == "__main__":
    fire.Fire(SymbolDataMongo)
