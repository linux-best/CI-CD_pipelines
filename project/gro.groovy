def log(x ,y ,z) {
    sh """
    sudo python3 project/log_msg.py ${x} ${y} ${z}
    """
}

def logmsg(n,m) {
    sh """
    sudo python3 project/hello.py ${n} "${m}"
    """
}

return this
