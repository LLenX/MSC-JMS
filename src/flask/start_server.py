#! /usr/bin/python3

from jmsserver import jms_server
import os
import sys
from getopt import gnu_getopt


def start_r_server():
    os.system('killall Rserve &> /dev/null')
    if os.system('Rscript -e "library(Rserve); Rserve()"') != 0:
        print('Fatal: Rserve didn\'t start correctly', file=sys.stderr,
              flush=True)
        sys.exit(1)
    else:
        print('Rserver start')
    sys.stdout.flush()
    sys.stderr.flush()


def get_port(argv):
    opts, args = gnu_getopt(argv, 'p:h:', ['port=', 'host='])
    print(opts)
    port, host = None, None
    for opt, value in opts:
        if opt == 'h' or opt == '--host':
            host = value
        elif opt == 'p' or opt == '--port':
            port = int(value)

    return port, host

if __name__ == '__main__':
    start_r_server()
    port, host = get_port(sys.argv[1:])
    if not port:
        port = 8080
    if not host:
        host = '0.0.0.0'
    jms_server.run(debug=True, port=port, host=host)
