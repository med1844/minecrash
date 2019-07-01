SIZE = 16
FULL_SIZE = 256


class Face(object):

    def __init__(self, texture_index, coord):
        self.coord = coord
        x1 = ((texture_index // (FULL_SIZE // SIZE)) * SIZE) / FULL_SIZE
        y1 = ((texture_index % (FULL_SIZE // SIZE)) * SIZE) / FULL_SIZE
        x2 = x1 + SIZE / FULL_SIZE
        y2 = y1 + SIZE / FULL_SIZE
        self.textureCoord = [(y, x) for x in [x1, x2] for y in [y1, y2]]

    def get(self):
        a, b, c, d = self.coord
        e, f, g, h = self.textureCoord
        return a + c + d + a + b + d, e + g + h + e + f + h


class Texture(object):

    def __init__(self):
        self.faces = []

    def get(self, up, down, left, right, front, back):
        self.faces.append(Face(up, [
            (x, .5, y) for x in [-.5, .5] for y in [-.5, .5]
        ]))
        self.faces.append(Face(down, [
            (x, -.5, y) for x in [-.5, .5] for y in [-.5, .5]
        ]))
        self.faces.append(Face(left, [
            (x, y, -.5) for y in [.5, -.5] for x in [.5, -.5]
        ]))
        self.faces.append(Face(right, [
            (x, y, .5) for y in [.5, -.5] for x in [.5, -.5]
        ]))
        self.faces.append(Face(front, [
            (.5, x, y) for x in [.5, -.5] for y in [.5, -.5]
        ]))
        self.faces.append(Face(back, [
            (-.5, x, y) for x in [.5, -.5] for y in [.5, -.5]
        ]))
        position, texture_coord = [], []
        for f in self.faces:
            assert isinstance(f, Face)
            pos, tex = f.get()
            position.extend(pos)
            texture_coord.extend(tex)
        return position, texture_coord


def pprint(position, texture_coord):
    pos_render, tex_render = [], []
    for i in range(len(position) // 3):
        pos_render.append(', '.join([str(position[i * 3 + d]) + 'f' for d in range(3) if i + d < len(position)]))
    for i in range(len(texture_coord) // 2):
        tex_render.append(', '.join([str(texture_coord[i * 2 + d]) + 'f' for d in range(2) if i + d < len(texture_coord)]))
    print(', \n'.join(pos_render))
    print(', \n'.join(tex_render))
    # assert len(pos_render) == len(tex_render)
    # for i in range(len(pos_render)):
    #     print("%s: %s" % (pos_render[i], tex_render[i]))


l = [Texture() for _ in range(10)]
# pprint(*l[1].get(0, 0, 0, 0, 0, 0))  # stone
pprint(*l[2].get(1, 3, 2, 2, 2, 2))  # grass
pprint(*l[4].get(4, 4, 4, 4, 4, 4))  # cobblestone
