import sys
import argparse
import requests
import urllib

def urljoin(*args):
    trailing_slash = '/' if args[-1].endswith('/') else ''
    return "/".join([str(x).strip("/") for x in args]) + trailing_slash

def build_url(base_url):
    url =  urljoin(base_url, 'files', args.filename)
    print(url)
    return url

def upload(args):
    files = {'upload_file': open(args.path, 'rb')}
    r = requests.post(build_url(args.url), files=files)
    print(r.status_code)

def delete(args):
    r = requests.delete(build_url(args.url))
    print(r.status_code)

def list_files(args):
    r = requests.get(urljoin(args.url, 'files'))
    print(r.content)
    return r

if __name__ == "__main__":
    parser = argparse.ArgumentParser("simple file storage client")
    subparsers = parser.add_subparsers(help='sub-command help')

    upload_parser = subparsers.add_parser('upload')
    upload_parser.add_argument('filename')
    upload_parser.add_argument('path')
    upload_parser.set_defaults(func=upload)

    delete_parser = subparsers.add_parser('delete')
    delete_parser.add_argument('filename')
    delete_parser.set_defaults(func=delete)

    list_parser = subparsers.add_parser('list')
    list_parser.set_defaults(func=list_files)

    parser.add_argument('--url')
    #parser.add_argument('--port', nargs=1)
    args = parser.parse_args()
    args.func(args)
