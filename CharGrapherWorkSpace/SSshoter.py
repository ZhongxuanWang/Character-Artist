from cv2 import *
import time
os.chdir("/Users/wangzhongxuan/IdeaProjects/CharGrapher-java/CharGrapherWorkSpace/")
while True:
    time.sleep(0.3)
    cam = VideoCapture(0)
    rep, img = cam.read()
    imwrite("SSGSHOTS_IMG.jpg",img)
