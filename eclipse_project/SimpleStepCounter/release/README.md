# SimpleStepCounter

Simple step counter for CLI,Java API

## function overview

- use as CLI,as automation for getting steps of many source files,aggregating,and so on.
- also use as Java API,for development of your programs/tools.
- various file type support,by customizing configuration files.

## how to use

### as CLI:

1.call SimpleStepCounter as below:
&nbsp;
for windwos:
<pre style="padding-left:10px;"><code>call SimpleStepCounter.bat [rootdir including files getting steps] [output file]
</code></pre>

for linux,other unix:  
<pre><code>./SimpleStepCounter.sh [rootdir including files getting steps] [output file]
</code></pre>

2.get \[output file\]. If need, also get log file(\[output file\].log):  
&nbsp;

3.use output file(tab divided text file),by looking text editor,copy into spreadsheet,use as another program input,and so on.  

### as Java API:

1.add SimpleStepCounter.jar into your classpath.If use Eclipse,import Eclipse project(eclipse_project/SimpleStepCounter) into your workspace.  

2.call API as below from you Java code.  
<pre><code>File yourfile;
&nbsp;
//get line count
util.stepcounter.StepCounter.getLineCount(yourfile);
&nbsp;
//get execution step count,excluding comments,empty lines
util.stepcounter.StepCounter.getExecStep(yourfile);  
</code></pre>


## various file type support

change StepCounter.properties as below:

1.add supported_filetypes(in this sample, add "cppsample")  
<pre><code>supported_filetypes=java,c,shell,general1,general2,cppsample  
</code></pre>

2.add configration for your types ,as below:  
<pre><code>\#extentions of appended filetype
cppsample.extentions=cpp,hpp
&nbsp;
\#block comment start string
cppsample.blockCommentStart=/*
&nbsp;
\#block comment end string
cppsample.blockCommentEnd=*/
&nbsp;
\#line comment start string
cppsample.lineCommentStart=//
&nbsp;
\#line comment end string
cppsample.lineCommentEnd=\n
&nbsp;
\#char literal start character
cppsample.charLiteralDelim='
&nbsp;
\#char literal escape character
cppsample.escapeChar_charLiteral=\\
&nbsp;
\#string literal start character
cppsample.stringLiteralDelim="
&nbsp;
\#string literal escape character
cppsample.escapeChar_stringLiteral=\\
</code></pre>

