Wikipedia-Search-Engine
========================

This repository consists of the mini project done as part of the course Information Retrieval and Extraction - Monsoon 2017. The course was instructed by [Dr. Vasudeva Varma](http://faculty.iiit.ac.in/~vv/Home.html). 

## Problem
The mini project involves building a search engine on the Wikipedia Data Dump without using any external index. For this project we use the data dump of 2013 of size 43 GB. The search results returns in real time. Multi word and multi field search on Wikipedia Corpus is implemented. SAX Parser is used to parse the XML Corpus. After parsing the following morphological operations are implemented:

    XML Parsing - Used default SAX parser from Java SE.
    Tokenization - Hand-coded tokenizer (without using regular expressions)
    Case folding - All tokens changed to lower case.
    Stop words removal - Wordnet (http://www.d.umn.edu/~tpederse/Group01/WordNet/words.txt)
    Stemming - Porter stemmer (http://tartarus.org/martin/PorterStemmer/java.txt)
    Posting List / Inverted Index creation
    Fetch documents by query (Tfidf rank)


The index, consisting of stemmed words and posting list is build for the corpus after performing the above operations along with the title and the unique mapping I have used for each document. Thus the document id of the wikipedia page is ignored. This helps in reducing the size as the document id do not begin with single digit number in the corpus. Since the size of the corpus will not fit into the main memory several index files are generated. Next, these index files are merged using K-Way Merge along with creating field based indices files.

For example, index0.txt, index1.txt, index2.txt are generated. These files may contain the same word. Hence, K Way Merge is applied and field based files are generated along with their respective offsets. These field based files are generated using multi-threading. This helps in doing multiple I/O simultaneously. Along with this the vocabulary file is also generated.

Along with these I have also stored the offsets of each of the field files. This reduces the search time to O(logm * logn) where m is the number of words in the vocabulary file and m is the number of words in the largest field file.
