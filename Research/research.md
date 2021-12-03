Research:

I tried using Jukebox. Browsed the material on the blog post by OpenAI about it, then installed the GitHub source code. A lot of trouble getting it installed properly; had to install NCCL various Python modules, and also Anaconda on Ubuntu. In the end we've decided to abandon it because it would've taken too long for the program to generate a few minutes of audio.

Our new idea is to have a mixer of looped ambient noises. Since we're communicating with front end, we will have to use Flask for Python. (Python server)

My role: 

Looping audio:

Mixing:

**Flask Server:**

Finding samples: