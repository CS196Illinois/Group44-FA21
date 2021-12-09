from flask import Flask
from flask import request
from samplecombiner import combinesamples # accepts file paths
from functools import reduce
import os

wav_list = []
app = Flask(__name__)

# route (JUST HOPE THIS WORKS)
@app.route('/', methods = ['POST', 'GET'])
# route function
def index():
  if request.method == 'POST':
    update_song_list(request) # update current list of strings
  if request.method == 'GET':
    return merged_wav(wav_list)



def update_song_list(request):
  song_list = request.form['song_list']

## accepts the names of the sounds, without file extension
## returns directly to client, not exported server-side
### which is probably inefficient in case of repeated GETs.
def merged_wav(wav_list):
  corrected = [os.path.realpath(f"wavesamplesshort/{rel}.wav") for rel in wav_list]
  ## below line is for demo purposes
  ## only supports 2 samples, because combinesamples takes in files but returns AudioSegment s
  result = reduce(lambda s1, s2: combinesamples(s1, s2), corrected)
  result.export(os.path.realpath(f"../test/result.wav"), format='wav')
  # return result

# listen
if __name__ == "__main__":
  app.run(debug=True, host='0.0.0.0')
  # visit http://localhost:5000/ to view site
  # if you need to make it live debuging add 'debug=True'  