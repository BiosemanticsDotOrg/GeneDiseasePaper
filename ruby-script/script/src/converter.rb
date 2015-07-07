# Concept profile generation and analysis for Gene-Disease paper
# Copyright (C) 2015 Biosemantics Group, Leiden University Medical Center
#  Leiden, The Netherlands
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published
# by the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
# 
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>

require 'rdf'
require 'slop'
require 'logger'
require 'zlib'
require 'rubygems'

class RDF_Converter

  HEADER_PREFIX = '#'

  def initialize (rdfNs, npNs, prefix)

    @options = get_options
    $base = RDF::Vocabulary.new(@options[:base_url])

    $saveFiles = true
    $createFileAt = 1000000

    # tracking converter progress
    @line_number = 0 # incremented after a line is read from input
    @row_index = 0 # incremented before a line is converted.
    @genesSkipped = 0


    @logger = Logger.new(STDOUT)
    @logger.level = Logger::INFO
    $logPrint = $createFileAt

    $RDF = rdfNs
    $NP = npNs
    $prefixes = prefix
  end

  def convert
    File.open(@options[:input], 'r') do |f|

      time_start = Time.now.utc
      time_start_rows = Time.now.utc

      while line = f.gets
        @line_number += 1
        if line =~ /^#{HEADER_PREFIX}/
          convert_header_row(line.strip)
        else
          convert_row(line.strip)
        end

        if @line_number % $logPrint == 0
          #$logger.info("============ running time for #{$logPrint} rows: #{(Time.now.utc - time_start_rows).to_s} ============")
          @logger.info("running time for #{$logPrint} rows: #{(Time.now.utc - time_start_rows).to_s}\tno of nanopublications so far: #{@row_index}\trows so far: #{@line_number}")
          time_start_rows = Time.now.utc
        end

      end
      @logger.info("running time total: #{(Time.now.utc - time_start).to_s}\tno of nanopublications = #{@row_index}")
      
    end

    if $saveFiles
      closeFile()
    end

  end 
  

  protected
  def convert_header_row(row)
    # do something
    puts "header: #{row}"
  end

  protected
  def convert_row(row)
    # do something
    @row_index += 1
    puts "row #{@row_index.to_s}: #{row}"
  end

  protected
  def save(ctx, triples)
    throw NotImplementedError.new
  end

  protected
  def get_options

    options = Slop.parse(:help => true) do
      on :i, :input=, 'input filename', :required => true
      on :o, :output=, 'output filename'
    end
    options.to_hash
  end

  private
  def create_main_graph(nanopub, assertion, provenance, publication_info)
    save(nanopub, [
        [nanopub, $RDF.type, $NP.Nanopublication],
        [nanopub, $NP.hasAssertion, assertion],
        [nanopub, $NP.hasProvenance, provenance],
        [nanopub, $NP.hasPublicationInfo, publication_info]
    ])
  end

end

class RDF_File_Converter < RDF_Converter

  def initialize(rdfNs, npNs, prefix)
    super(rdfNs, npNs, prefix)
    $saveFiles = true
    $totalStatements = 0
    $filesCreated = 0
    $NoOfStatements = 0
    $file = nil
    $time_start = 0

  end



  def save(context, triples)
    triples.each do |subject, predicate, object|

      if $NoOfStatements == 0

        $filesCreated += 1
        outputFile =  "#{@options[:output]}_#{$filesCreated}.nq.gz"
        $file = Zlib::GzipWriter.open(outputFile)
        $time_start = Time.now.utc
      end

      #@file << RDF::Statement(subject.to_uri, predicate, object, :context => context.to_uri)
      if object.literal?
        objectLiteral = ("\"#{object.to_s}\"^^<#{object.datatype}>")
        $file << ("<#{subject.to_uri}> <#{predicate.to_uri}> #{objectLiteral} <#{context.to_uri}> .")
      else
        $file << ("<#{subject.to_uri}> <#{predicate.to_uri}> <#{object.to_s}> <#{context.to_uri}> .")
      end

      $file << "\n"

      $NoOfStatements += 1

      if $NoOfStatements == $createFileAt
        closeFile()
      end
    end

  end

  def closeFile()

    $totalStatements = $totalStatements + $NoOfStatements
    $NoOfStatements = 0

    puts "No of statements in a file #{$totalStatements}"
    if $file != nil
      $file.close
    end
    @logger.info((Time.now.utc - $time_start).to_s)
  end

end

class RDF_Nanopub_Converter < RDF_Converter


  def initialize(rdfNs, npNs, prefix)

    super(rdfNs, npNs, prefix)

    @server = AllegroGraph::Server.new(:host => @options[:host], :port => @options[:port],
                                       :username => @options[:username], :password => @options[:password])

    @catalog = @options[:catalog] ? AllegroGraph::Catalog.new(@server, @options[:catalog]) : @server
    @repository = @RDF::AllegroGraph::Repository.new(:server => @catalog, :id => @options[:repository])

    if @options[:clean]
      @repository.clear
    elsif @repository.size > 0 && !@options[:append]
      puts "repository is not empty (size = #{@repository.size}). Use --clean to clear repository before import, or use --append to ignore this setting."
      exit 1
    end
  end

  protected
  def save(context, triples)
    triples.each do |subject, predicate, object|
      @repository.insert([subject.to_uri, predicate, object, context.to_uri])
    end
  end

  protected
  def get_options
    options = Slop.parse(:help => true) do
      on :host=, 'allegro graph host, default=localhost', :default => 'localhost'
      on :port=, 'default=10035', :as => :int, :default => 10035
      on :catalog=
      on :repository=, :required => true
      on :username=
      on :password=
      on :clean, 'clear the repository before import', :default => false
      on :append, 'allow adding new triples to a non-empty triple store.', :default => false
    end

    super.merge(options)
  end


end