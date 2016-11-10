#!/usr/bin/env python
#-*- coding:utf-8 -*-

import codecs
import sys

def encode_gbk_utf8(flieaddr):
    content = None
    with codecs.open(fileaddr, mode='r', encoding="gbk") as f:
        content = f.read()
    with codecs.open(fileaddr, mode='w', encoding="utf-8") as f:
        f.write(content)


if __name__ == "__main__":
    if sys.argv and len(sys.argv) == 2:
        fileaddr = sys.argv[1]
        print "deal with", fileaddr
        encode_gbk_utf8(fileaddr)
    else:
        print "Error!"

