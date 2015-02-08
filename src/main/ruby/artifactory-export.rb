require 'json'
require 'net/http'
require 'uri'
require 'fileutils'


artifactory_url = 'https://evgenyg.artifactoryonline.com/evgenyg'
repo_name       = 'repo1-cache'
matcher         = '\.(jar)|(pom)$'
max_size        = 256000
username        = '...'
password        = '...'
directory       = "#{ENV['HOME']}/export"
sleep_period    = 5


def read_files( artifactory_url, repo_name, username, password )
  # https://www.jfrog.com/confluence/display/RTF/Artifactory+REST+API#ArtifactoryRESTAPI-FileList
  # https://evgenyg.artifactoryonline.com/evgenyg/api/storage/jcenter-cache?list&deep=1&listFolders=0
  # http://www.rubyinside.com/nethttp-cheat-sheet-2940.html
  raise "Artifactory URL should start with 'https://'" unless artifactory_url.start_with?( 'https://' )
  url          = "#{artifactory_url}/api/storage/#{repo_name}?list&deep=1&listFolders=0"
  uri          = URI.parse( url )
  http         = Net::HTTP.new( uri.host, uri.port )
  http.use_ssl = true
  request      = Net::HTTP::Get.new( uri.request_uri )
  request.basic_auth( username, password )
  puts "Requesting [#{url}]"
  response     = http.request( request )
  body         = response.body
  puts "Requesting [#{url}] - [#{response.code}]"
  raise "Failed to execute request '#{url}': #{body}" unless response.code.to_s == '200'
  files        = JSON.parse( body )['files']
  raise "Response has no 'files': #{body}" if files.nil?
  files
end


def read_urls( files, matcher = '^.+$', max_size = 9223372036854775807 )
  raise 'Files are undefined' if files.nil?
  urls   = []
  regexp = Regexp.new( matcher )
  files.each { |file|
    url  = file['uri']
    size = ( file['size'] || 0 ).to_i
    urls << url if (( regexp =~ url ) && ( size <= max_size ))
  }
  puts "Found [#{urls.size}] matching urls"
  urls
end


def download_files( artifactory_url, repo_name, directory, urls, sleep_period )
  urls.each_with_index { |url, j|
    target  = File.absolute_path( "#{directory}#{url}" )
    FileUtils.mkdir_p( File.dirname( target ))
    command = "wget -nv '#{artifactory_url}/#{repo_name}#{url}' -O '#{target}'"
    puts "Running #{j+1}/#{urls.size} - [#{command}]"
    `#{command}`
    sleep sleep_period if sleep_period > 0
  }
end


urls = read_urls( read_files( artifactory_url, repo_name, username, password ), matcher, max_size )
download_files( artifactory_url, repo_name, directory, urls, sleep_period )
