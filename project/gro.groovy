def logmsg(x,y,z) {
    sh """
    sudo python3 project/hello.py ${x} "${y}" "${z}"
    """
}

return this
