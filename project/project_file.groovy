def logmsg(x,y,z) {
    sh """
    sudo python3 project/logMSG_1.py ${x} "${y}" "${z}"
    """
}

return this
