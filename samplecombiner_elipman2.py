# -*- coding: utf-8 -*-
"""
Created on Sun Nov 28 21:18:51 2021

@author: ellio
"""

from pydub import AudioSegment



#function for combining multiple samples given names of samples (array must not be null):
def combinemanysamples(array):
    wavearray = []
    for samplename in array:      #create a list of sample files from sample names
        if namemap[samplename] is not None:
            wavearray.append(namemap[samplename])
        
    lengths = []
    for sample in wavearray:        #create an array of samples lengths
        lengths.append(len(sample))
        
    samplesbylength = [x for _,x in sorted(zip(lengths, array))] #sort it
    output = AudioSegment.silent(duration = len(samplesbylength[0])) #create a silent sample with length of shortest sample
    
    for sample in wavearray:        #overlay each sample onto the silent file
        output = output.overlay(sample)
        
    return output

namemap = {"city": AudioSegment.from_file(r"C:\\Users\ellio\Downloads\wavesamplesshort\city.wav"),
           "fireplace": AudioSegment.from_file(r"C:\\Users\ellio\Downloads\wavesamplesshort\fireplace.wav"),
           "nature": AudioSegment.from_file(r"C:\\Users\ellio\Downloads\wavesamplesshort\nature.wav"),
           "rain": AudioSegment.from_file(r"C:\\Users\ellio\Downloads\wavesamplesshort\rain.wav"),
           "restaurant": AudioSegment.from_file(r"C:\\Users\ellio\Downloads\wavesamplesshort\restaurant.wav"),
           "whitenoise": AudioSegment.from_file(r"C:\\Users\ellio\Downloads\wavesamplesshort\whitenoise.wav")
           }

songnames = ["city", "fireplace", "nature"]
combinemanysamples(songnames).export(r"C:\\Users\ellio\Downloads\output.wav", format="wav")