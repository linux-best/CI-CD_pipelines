import sys

from loguru import logger

a = sys.argv[1]

if a == "info" :
    print(f"log_msg=> {sys.argv[1]}")
