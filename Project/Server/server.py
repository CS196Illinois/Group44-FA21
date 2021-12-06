from flask import Flask
from flask import request



app = Flask(__name__)
song_list = []

# route
@app.route('/', methods = ['POST', 'GET'])
# route function
def index():
  if request.method == 'POST':
    update_song_list(request) # update current list of strings
  if request.method == 'GET':
    return merged_wav(request)



def update_song_list(request):
  song_list = request.form['song_list']

def merged_wav(request):
  ## TODO use eliots function

if request.method == 'GET':
  ... # give the merged file


# listen
if __name__ == "__main__":
  app.run(debug=True, host='0.0.0.0')
  # visit http://localhost:5000/ to view site
  # if you need to make it live debuging add 'debug=True'  