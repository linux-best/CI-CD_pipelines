def logmsg(x,y,z) {
    sh """
    sudo python3 project/logMSG.py ${x} "${y}" "${z}"
    """
}

return this
