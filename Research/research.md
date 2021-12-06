Research:

I tried using Jukebox. Browsed the material on the blog post by OpenAI about it, then installed the GitHub source code. A lot of trouble getting it installed properly; had to install NCCL various Python modules, and also Anaconda on Ubuntu. In the end we've decided to abandon it because it would've taken too long for the program to generate a few minutes of audio.

Our new idea is to have a mixer of looped ambient noises. Since we're communicating with front end, we will have to use Flask for Python. (Python server)

My role: 

**Flask Server:** Create a flask server that can post a wav file and can get a list of strings. 
Upon getting a post request, you should replace the current list of strings you have with the new ones inputted.
Upon getting a get request, you should input the current list of strings into Elliot's function, and then send back the merged wav file. 