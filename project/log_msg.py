# my_script.py
import sys
from loguru import logger

path="/var/lib/jenkins/logs/pipeline.log"

logger.add(path ,
           format="<yellow>{time: MMMM D, YYYY - HH:mm:ss}</yellow> -- <green>{level}</green> -- <level>{message}</level> {extra}",
           level="DEBUG"
)

x = sys.argv[1]
y = sys.argv[2]
z = sys.argv[3]

if y == "info" :
    logger.info("log msg")
elif y == "success" :
    logger.success("log msg")
elif y == "error" :
    logger.error("log msg")

#print(f"Script name: {sys.argv[0]}") # [0] is always the anme of the script
#if len(sys.argv) > 1:
#    print(f"Argument 1: {sys.argv[1]}") # first argument
#    print(f"Argument 2: {sys.argv[2]}") # second argument
