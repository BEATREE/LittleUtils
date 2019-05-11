import requests
import os
from urllib import request
from http.cookiejar import CookieJar
from bs4 import BeautifulSoup

header = {'User-Agent': 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.163 Safari/535.1',
          'Referer': 'http://www.mm131.com',
          'Accept-Encoding': 'gzip, deflate',
          'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
          'Upgrade-Insecure-Requests': '1',
          'Connection': 'keep-alive'
          }

def get_opener():
    # 1. 登录
    # 1.1  创建一个cookiejar对象
    cookiejar = CookieJar()
    # 1.2  使用cookiejar创建一个HTTPCookieProcess对象
    handler = request.HTTPCookieProcessor(cookiejar)
    # 1.3  使用上一步骤创建的handler创建一个opener
    opener = request.build_opener(handler)
    return opener


def getHref(allText, storePath, opener):
    hrefList = []
    titleList = []
    soup = BeautifulSoup(allText, 'lxml')   # BeautifulSoup 提取分析
    mainDiv = soup.find("div", class_="main_top").find("dl", class_="hot")  # 查询出div标签下的热门排行代码块
    # print(mainDiv)
    dds = mainDiv.findAll("dd")  # 获取存放网页链接的dd列表
    for dd in dds:
        try:
            href = dd.find("a").attrs['href']
            title = dd.find("a").text
            # print(title, "  ", href)
            hrefList.append(href)
            titleList.append(title)
        except OSError:
            pass
        continue

    getPic(titleList, hrefList, storePath)

def getPic(titleList, hrefList, storePath):    # 获取图片 和 存储路径
    srcList = []  # 用于存放连接
    if not os.path.exists(storePath+"\\你懂的"):  # 如果存在当前目录
        os.mkdir(storePath + "\\你懂的")  # 创建目录

    for i in range(len(hrefList)):   # 循环下载排行榜
        print("开始下载", titleList[i], "专辑...")
        singleHtml = requests.get(hrefList[i], headers=header)
        singleHtml.encoding = singleHtml.apparent_encoding
        soup = BeautifulSoup(singleHtml.text, 'lxml')   # 开始使用BeautifulSoup
        contentDiv = soup.find("div", class_="content")  # 查询出div标签下的内容块‘
        picNum = contentDiv.find("div", class_="content-page").find("span", class_="page-ch").text[1:3] #获取总页数
        # http: // img1.mm131.me / pic / 3245 / 1.jpg
        # http: // img1.mm131.me / pic / 3245 / 1.jpg
        originalSrc = contentDiv.find("div", class_="content-pic").find("a").find("img").attrs["src"].split("/") # 找到存储路径
        singleSrc = originalSrc[0]+"/"+originalSrc[1]+"/"+originalSrc[2]+"/"+originalSrc[3]+"/"+originalSrc[4]+"/"
        picType = originalSrc[5][-4:]   # 图片类型
        for index in range(eval(picNum)):
            srcList.append(singleSrc+str(index+1)+picType)

    downLoadPic(titleList, storePath, srcList)

def downLoadPic(titleList, storePath, srcList):
    # print(srcList)
    # print(titleList)
    for src in range(len(srcList)):
        try:
            s = src+1
            header["Host"] = "img1.mm131.me"
            content = requests.get(srcList[src], headers=header)
            # print(content)
            # requests.add_header()
            # print(opener.open(srcList[src]))
            # print(src)
            with open(storePath + "\\你懂的\\" + str(s) + srcList[src][-4:], 'wb') as f:
                f.write(content.content)
                # print(content.content)
                f.close()
        except OSError:
            pass


if __name__ == '__main__':
    opener = get_opener()
    print("======================================================")
    print("在你使用之前，先搞清楚这个是干什么的")
    print("什么？你不知道？那你下他干什么啊？是不是想放飞自我？！")
    print("那我告诉你吧，它有关Sexy，哈~")
    print("website: http://www.teenshare.club & author: BEATREE")
    print("======================================================")
    storagePath = input("要下载？那就请输入存储路径吧：")
    allHtml = requests.get("http://www.mm131.com/", headers=header)  # 返回的是response对象而不是网页内容
    opener.open(request.Request("http://www.mm131.com/"))
    allHtml.encoding = allHtml.apparent_encoding
    getHref(allHtml.text, storagePath, opener)
