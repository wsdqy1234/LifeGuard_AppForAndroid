import cv2
cap = cv2.VideoCapture(0)
cap.set(cv2.CAP_PROP_FRAME_WIDTH,640)
cap.set(cv2.CAP_PROP_FRAME_HEIGHT,480)

for i in range(100):
    f, frame = cap.read()
    cv2.imwrite(str(i)+'.png',frame)
cap.release