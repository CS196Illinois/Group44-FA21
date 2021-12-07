# -*- coding: utf-8 -*-
"""
Created on Sun Nov 28 21:18:51 2021

@author: ellio
"""

from pydub import AudioSegment

def combinesamples(s1, s2): #combine two samples given paths s1 and s2
    sound1 = AudioSegment.from_file(s1)
    sound2 = AudioSegment.from_file(s2)
    return sound1.overlay(sound2)


s1 = "C:\\Users\ellio\Downloads\sample1.wav"
s2 = "C:\\Users\ellio\Downloads\sample2.wav"

combinesamples(s1,s2).export("C:\\Users\ellio\Downloads\samplecombined2.wav", format='wav')