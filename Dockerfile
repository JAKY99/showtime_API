FROM kevinpsirius/javadeployspring:v1
COPY ./ /var/www/html/app
WORKDIR /app
CMD tail -f /dev/null