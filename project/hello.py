
import sys
from loguru import logger

path="/var/lib/jenkins/logs/pipeline.log"

logger.add(path ,
           format="<yellow>{time: MMMM D, YYYY - HH:mm:ss}</yellow> -- <green>{level}</green> -- <level>{message}</level> {extra}",
           level="DEBUG"
)

a = sys.argv[1]

if a == "amir" :
    logger.info("log")
