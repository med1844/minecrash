SIZE = 16
FULL_SIZE = 256


class Face(object):

    def __init__(self, texture_index, coord):
        self.x1 = ((texture_index // (FULL_SIZE // SIZE)) * SIZE) / FULL_SIZE
        self.y1 = ((texture_index % (FULL_SIZE // SIZE)) * SIZE) / FULL_SIZE
        self.x2 = self.x1 + SIZE / FULL_SIZE
        self.y2 = self.y1 + SIZE / FULL_SIZE
        print(coord)


class Texture(object):

    def __init__(self):
        self.l = []

    def set(self, up, down, left, right, front, back):
        self.l.append(Face(up, [
            (x, 0.5, y) for x in [-.5, .5] for y in [-.5, .5]
        ]))
        self.l.append(Face(down, [
            (x, 0.5, y) for x in [-.5, .5] for y in [-.5, .5]
        ]))
        self.l.append(Face(right, [
            ()
        ]))


l = [Texture() for _ in range(10)]
l[1].set(0, 0, 0, 0, 0, 0)  # stone
l[2].set(1, 3, 2, 2, 2, 2)  # grass

