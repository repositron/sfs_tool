import sys
import argparse
import requests

def upload(args):
    files = {'upload_file': open(args.filename, 'rb')}
    r = requests.post(args.url, files=files)

def delete(args):
    files = {'upload_file': open(args.filename, 'rb')}
    r = requests.delete(args.url)

def requestUrl(url):
    

if __name__ == "__main__":
    parser = argparse.ArgumentParser("simple file storage client")
    subparsers = parser.add_subparsers(help='sub-command help')

    u = subparsers.add_parser('upload')
    u.add_argument('filename')
    u.set_defaults(func=upload)

    d = subparsers.add_parser('delete')
    d.add_argument('filename')
    d.set_defaults(func=delete)

    subparsers.add_parser('list')
    parser.add_argument('--url', nargs=1)
    parser.add_argument('--port', nargs=1)
    args = parser.parse_args()
    args.func(args)
