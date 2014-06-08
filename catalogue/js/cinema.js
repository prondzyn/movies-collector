jQuery.fn.dataTableExt.oSort['cinemaString-asc'] = function(x,y) {
  var retVal;
  x = $.trim(x);
  y = $.trim(y);

  if (x==y) retVal= 0;
  else if (x == "" || x == "&nbsp;") retVal=  1;
  else if (y == "" || y == "&nbsp;") retVal=  -1;
  else if (x > y) retVal=  1;
  else retVal = -1;  // <- this was missing in version 1

  return retVal;
}
jQuery.fn.dataTableExt.oSort['cinemaString-desc'] = function(y,x) {
  var retVal;
  x = $.trim(x);
  y = $.trim(y);

  if (x==y) retVal= 0; 
  else if (x == "" || x == "&nbsp;") retVal=  -1;
  else if (y == "" || y == "&nbsp;") retVal=  1;
  else if (x > y) retVal=  1;
  else retVal = -1; // <- this was missing in version 1

  return retVal;
}

function prepareMeta(movieInfo, valueIfEmpty) {
  var initialMeta = movieInfo.meta;
  var format = movieInfo.format;
  if (initialMeta) {
    var meta = initialMeta.replace(format,'').trim();
    return meta || valueIfEmpty;
  } else {
    return valueIfEmpty;
  }
}

function prepareYear(movieInfo, valueIfEmpty, valueIfNotApplicable) {
  var year = movieInfo.year;
  if (year == 'NA') {
    return valueIfNotApplicable;
  } else if (!(year)) {
    return valueIfEmpty;
  } else {
    return year;
  }
}

function prepareTitle(title, meta) {
  if (title && meta) {
    return clearFirstMetaElement(title, meta).trim();
  } else if (title) {
    return title;
  } else {
    return '';
  }
}

function clearFirstMetaElement(string, meta) {
  var table = meta.split(' ');
  var result = string.replace(table[0],'').trim();
  return result;
}

function filmwebHref(title) {
  return 'http://www.filmweb.pl/search?q='+title;
}