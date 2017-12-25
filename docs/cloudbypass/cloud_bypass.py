from flask import Flask, jsonify
from flask import request
import cfscrape

app = Flask(__name__)

# When localhost access through 'http://127.0.0.1:5000/api/v1/content?url=http://animeflv.net/ver/one-piece-1.baseLink'
@app.route('/v1/content', methods=('get', 'post'))
def get_tasks():
    page='null';
    try:
        url = request.args.get('url')
        type = request.args.get('type')

        if type == 'baseLink':
            scraper = cfscrape.create_scraper()
            page = scraper.get(url).content
            return page

        if type == 'cookies':
            scraper = cfscrape.create_scraper()
            tokens, user_agent = cfscrape.get_tokens(url)
            response = {
                'url': url,
                'user_agent': user_agent,
                'tokens_cookies': tokens
            }
            return jsonify(response)
    except:
        return page

if __name__ == "__main__":
    app.run()