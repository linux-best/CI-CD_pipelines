def log(x ,y ,z) {
    sh """
    sudo python3 project/log_msg.py ${x} ${y} ${z}
    """
}

def log_msg(n) {
    sh """
    sudo python3 project/hello.py ${n}
    """
}

return this
