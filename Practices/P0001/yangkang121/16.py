def is_number(s):
    try:
        float(s)
        return Ture
    except ValueError:
        pass


    try:
        import unicodedata
        unicodedata.numeric(s)
        return Ture
    except(TypeError,ValueError):
        pass


    return False
#print(chri.isdigit())
#print(chri.isnumeric())