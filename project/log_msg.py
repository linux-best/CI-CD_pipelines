# my_script.py
import sys
from loguru import logger

path="/var/lib/jenkins/logs/pipeline.log"

logger.add(path ,
           format="<yellow>{time: MMMM D, YYYY - HH:mm:ss}</yellow> -- <green>{level}</green> -- <level>{message}</level> {extra}",
           level="DEBUG"
)

print(sys.argv[1],sys.argv[2],sys.argv[3])

match sys.argv[2] :
    case "1" :
        with logger.contextualize(log=sys.argv[3]):
            logger.info(sys.argv[1])
    case "2" :
        with logger.contextualize(log=sys.argv[3]):
            logger.success(sys.argv[1])
    case "3" :
        with logger.contextualize(log=sys.argv[3]):
            logger.error(sys.argv[1])                

#print(f"Script name: {sys.argv[0]}") # [0] is always the anme of the script
#if len(sys.argv) > 1:
#    print(f"Argument 1: {sys.argv[1]}") # first argument
#    print(f"Argument 2: {sys.argv[2]}") # second argument
