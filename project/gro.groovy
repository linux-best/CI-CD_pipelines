def log(x ,y ,z) {
    sh "python3 project/log_msg.py ${x} ${y} ${z}"
}

return this
